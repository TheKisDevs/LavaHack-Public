/*******************************************************************************
 * Copyright (c) 2011 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.lib.jse;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.mapping.ForgeMappings;
import com.kisman.cc.catlua.mapping.RemapperUtil;
import fuck.you.yarnparser.entry.*;
import org.luaj.vm2.LuaValue;

/**
 * LuaValue that represents a Java class.
 * <p>
 * Will respond to get() and set() by returning field values, or java methods.
 * <p>
 * This class is not used directly.
 * It is returned by calls to {@link CoerceJavaToLua#coerce(Object)}
 * when a Class is supplied.
 * @see CoerceJavaToLua
 * @see CoerceLuaToJava
 */
class JavaClass extends JavaInstance implements CoerceJavaToLua.Coercion {

	static final Map classes = Collections.synchronizedMap(new HashMap());

	static final LuaValue NEW = valueOf("new");

	Map fields;
	Map methods;
	Map innerclasses;

	static JavaClass forClass(Class c) {
		JavaClass j = (JavaClass) classes.get(c);
		if ( j == null ) classes.put( c, j = new JavaClass(c) );
		return j;
	}

	JavaClass(Class c) {
		super(c);
		this.jclass = this;
	}

	public LuaValue coerce(Object javaValue) {
		return this;
	}

	Field getField(LuaValue key) {
		if ( fields == null ) {
			Map m = new HashMap();
			Field[] f = ((Class)m_instance).getFields();
			for ( int i=0; i<f.length; i++ ) {
				Field fi = f[i];
				if ( Modifier.isPublic(fi.getModifiers()) ) {
					m.put(LuaValue.valueOf(fi.getName()), fi);
					try {
						if (!fi.isAccessible()) fi.setAccessible(true);
					} catch (SecurityException ignored) {}
				}
			}
			fields = m;
		}

		if(Kisman.remapped) {
			Class<?> superclass = m_instance.getClass();
			String name = superclass.getName() + key.tojstring();
			if(Kisman.instance.remapper3000.fieldsCache.containsKey(name)) return Kisman.instance.remapper3000.fieldsCache.get(name);

			if(RemapperUtil.Companion.canBeRemapped(superclass.getName())) {
				while (superclass != null) {
//				System.out.println("FindField: superclass " + superclass.getName() + ", name " + key.tojstring());
					System.out.println("Class:" + superclass.getName());
					FieldEntry fieldEntry = Kisman.instance.forgeMappings.findField(superclass.getName().replace(".", "/"), key.tojstring(), ForgeMappings.NormalFindType.OFFICIAL);
					if (fieldEntry != null) {
						Kisman.instance.remapper3000.fieldsCache.put(name, (Field) fields.get(LuaValue.valueOf(fieldEntry.intermediary)));
						System.out.println(fields.get(LuaValue.valueOf(fieldEntry.intermediary)));
						return (Field) fields.get(LuaValue.valueOf(fieldEntry.intermediary));
					}
					superclass = superclass.getSuperclass();
				}

				Kisman.instance.remapper3000.fieldsCache.put(name, (Field) fields.get(key));
			}
		}

		System.out.println(fields.get(key));
		return (Field) fields.get(key);
	}

	LuaValue getMethod(LuaValue key) {
		if ( methods == null ) {
			Map namedlists = new HashMap();
			Method[] m = ((Class)m_instance).getMethods();
			for ( int i=0; i<m.length; i++ ) {
				Method mi = m[i];
				if ( Modifier.isPublic( mi.getModifiers()) ) {
					String name = mi.getName();
					List list = (List) namedlists.get(name);
					if ( list == null ) namedlists.put(name, list = new ArrayList());
					list.add( JavaMethod.forMethod(mi) );
				}
			}
			Map map = new HashMap();
			Constructor[] c = ((Class)m_instance).getConstructors();
			List list = new ArrayList();
			for ( int i=0; i<c.length; i++ ) if ( Modifier.isPublic(c[i].getModifiers()) ) list.add( JavaConstructor.forConstructor(c[i]) );
			switch ( list.size() ) {
				case 0: break;
				case 1: map.put(NEW, list.get(0)); break;
				default: map.put(NEW, JavaConstructor.forConstructors( (JavaConstructor[])list.toArray(new JavaConstructor[list.size()]) ) ); break;
			}

			for ( Iterator it=namedlists.entrySet().iterator(); it.hasNext(); ) {
				Entry e = (Entry) it.next();
				String name = (String) e.getKey();
				List methods = (List) e.getValue();
				map.put( LuaValue.valueOf(name), methods.size()==1? methods.get(0): JavaMethod.forMethods( (JavaMethod[])methods.toArray(new JavaMethod[methods.size()])) );
			}
			methods = map;
		}

		if(Kisman.remapped) {
			Class superclass = ((Class) m_instance);
			String name = superclass.getName() + key.tojstring();
			if(Kisman.instance.remapper3000.methodsCache.containsKey(name)) Kisman.instance.remapper3000.methodsCache.get(name);

			while(superclass != null) {
//				System.out.println("FindMethod: superclass " + superclass.getName() + ", name " + key.tojstring());
				MethodEntry methodEntry = Kisman.instance.forgeMappings.findMethod(superclass.getName().replace(",", "/"), key.tojstring(), ForgeMappings.NormalFindType.NAMED, -1, null);
				if(methodEntry != null) {
					Kisman.instance.remapper3000.methodsCache.put(name, (LuaValue) methods.get(LuaValue.valueOf(methodEntry.intermediary)));
					return (LuaValue) methods.get(LuaValue.valueOf(methodEntry.intermediary));
				}
				superclass = superclass.getSuperclass();
			}

			Kisman.instance.remapper3000.methodsCache.put(name, (LuaValue) methods.get(key)); // <- probably null
		}

		return (LuaValue) methods.get(key);
	}

	Class getInnerClass(LuaValue key) {
		if ( innerclasses == null ) {
			Map m = new HashMap();
			Class[] c = ((Class)m_instance).getClasses();
			for ( int i=0; i<c.length; i++ ) {
				Class ci = c[i];
				String name = ci.getName();
				String stub = name.substring(Math.max(name.lastIndexOf('$'), name.lastIndexOf('.'))+1);
				m.put(LuaValue.valueOf(stub), ci);
			}
			innerclasses = m;
		}
		return (Class) innerclasses.get(key);
	}

	public LuaValue getConstructor() {
		return getMethod(NEW);
	}
}
