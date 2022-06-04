//--- hatsuyuki ---
// by Catzpaw 2016
#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform sampler2D texture;

float snow(vec2 uv,float scale)
{
	float w=smoothstep(1.,0.,-uv.y*(scale/10.));
	if(w<.1) return 0.;
	uv+=time/scale;uv.y+=time*2./scale;uv.x+=sin(uv.y+time*.5)/scale;
	uv*=scale;vec2 s=floor(uv),f=fract(uv),p;float k=3.,d;
	p=.5+.35*sin(11.*fract(sin((s+p+scale)*mat2(7,3,6,5))*5.))-f;d=length(p);k=min(d,k);
	k=smoothstep(0.,k,sin(f.x+f.y)*0.01);
	return k*w;
}

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

void main(){
	vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

	vec2 uv=(gl_FragCoord.xy*2.-resolution.xy)/min(resolution.x,resolution.y); 
	vec3 finalColor=vec3(0);
	float c=smoothstep(1.,0.3,clamp(uv.y*.3+.8,0.,.75));
	c+=snow(uv,30.)*.0;
	c+=snow(uv,20.)*.0;
	c+=snow(uv,15.)*.0;
	c+=snow(uv,10.);
	c+=snow(uv,8.);
	c+=snow(uv,6.);
	c+=snow(uv,5.);
	finalColor = vec3(c);
	finalColor *= vec3(.7, 0.5, 1.);

	float alpha = 0;
	if (centerCol.a != 0) {
		float sum = finalColor.r + finalColor.g + finalColor.b;
		if (sum > 1.0) {
			alpha = 1.0 - (sum / 6);
		} else {
			alpha = 1.0 - finalColor.r;
		}
	} else {
		alpha = glowShader();
	}

	gl_FragColor = vec4(finalColor, alpha);
}
