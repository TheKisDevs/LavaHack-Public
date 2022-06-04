#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;
uniform sampler2D texture;

#define iterations 15

#define formuparam 0.330

#define volsteps 12
#define stepsize 0.10

#define zoom   2.0
#define tile   0.40
#define speed  .01

#define brightness 0.0019
#define darkmatter 0.40
#define distfading 0.99
#define saturation 1.60

float glowShader() {
	float radius = 2.5;
	float quality = 1.0;
	float divider = 158.0;
	float maxSample = 10.0;
	vec2 texelSize = vec2(1.0 / resolution.x * (radius * quality), 1.0 / resolution.y * (radius * quality));
	float alpha = 0;

	for (float x = -radius; x < radius; x++) {
		for (float y = -radius; y < radius; y++) {
			vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

			if (currentColor.a != 0)
			alpha += divider > 0 ? max(0.0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : 1;
		}
	}

	return alpha;
}

void main() {
	vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

	//get coords and direction
	vec2 uv=gl_FragCoord.xy/resolution.xy-.5;
	uv.y*=resolution.y/resolution.x;
	vec3 dir=vec3(uv*zoom,1.);
	
	float a2=time*speed+.5;
	float a1=5.0;
	mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
	mat2 rot2=rot1;//mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
	dir.xz*=rot1;
	dir.xy*=rot2;
	
	vec3 from=vec3(0.,0.,0.);
	from+=vec3(.0*time,.1*time, 0);
		
	from.xz*=rot1;
	from.xy*=rot2;
	
	//volumetric rendering
	float s=.1,fade=.05;
	vec3 v=vec3(.05);
	for (int r=0; r<volsteps; r++) {
		vec3 p=from+s*dir*1.5;
		p = abs(vec3(tile)-mod(p,vec3(tile*2.))); // tiling fold
		p.x+=float(r*r)*0.01;
		p.y+=float(r)*0.02;
		float pa,a=pa=0.;
		//Creates the Particles
		for (int i=0; i<iterations; i++) { 
			p=abs(p)/dot(p,p)-formuparam; // the magic formula
			a+=abs(length(p)-pa*0.1); // absolute sum of average change
			pa=length(p);
		}
		float dm=max(0.1,darkmatter-a*a*.9); //dark matter
		a*=a*a*2.; // add contrast
		if (r>3) fade*=1.-dm; // dark matter, don't render near
		v+=vec3(dm,dm*.0,0.6);
		v+=fade;
		v+=vec3(s,s*s,s*s*s*s*s)*a*brightness*fade; // coloring based on distance
		fade*=distfading; // distance fading
		s+=stepsize;
	}
	v=mix(vec3(length(v)),v,saturation); //color adjust

	float alpha = 0;
	if (centerCol.a != 0) {
		alpha = 1.0;
	} else {
		alpha = glowShader();
	}

	gl_FragColor = vec4(v*.01, alpha);
}