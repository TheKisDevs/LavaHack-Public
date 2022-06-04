

#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform vec2      resolution;
uniform float     time;
uniform vec2      speed;
uniform float     shift;
uniform sampler2D texture;

uniform vec3 color;
uniform float radius;
uniform float quality;
uniform float divider;
uniform float maxSample;

float rand(vec2 n) {
  //This is just a compounded expression to simulate a random number based on a seed given as n
  	return fract(cos(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 n) {
  //Uses the rand function to generate noise
	  const vec2 d = vec2(0.0, 1.0);
	  vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
	  return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

float fbm(vec2 n) {
  //fbm stands for "Fractal Brownian Motion" https://en.wikipedia.org/wiki/Fractional_Brownian_motion
	  float total = 0.0, amplitude = 1.6;
	  for (int i = 0; i < 4; i++) {
 	   total += noise(n) * amplitude;
	    n += n;
	    amplitude *= 0.5;
	  }
	  return total;
}

float glowShader() {
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

float calcLuma(vec3 color) {
    return 0.299*color.r + 0.587*color.g + 0.114*color.b;
}

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    //This is where our shader comes together
    const vec3 c1 = vec3(126.0/255.0, 0.0/255.0, 97.0/255.0);
    const vec3 c2 = vec3(173.0/255.0, 0.0/255.0, 161.4/255.0);
    const vec3 c3 = vec3(0.2, 0.0, 0.0);
    const vec3 c4 = vec3(164.0/255.0, 1.0/255.0, 214.4/255.0);
    const vec3 c5 = vec3(0.1);
    const vec3 c6 = vec3(0.9);
    
    //This is how "packed" the smoke is in our area. Try changing 8.0 to 1.0, or something else
    vec2 p = gl_FragCoord.xy * 8.0 / resolution.xx;
    //The fbm function takes p as its seed (so each pixel looks different) and time (so it shifts over time)
    float q = fbm(p - time * 0.1);
    vec2 r = vec2(fbm(p + q + time * speed.x - p.x - p.y), fbm(p + q - time * speed.y));
    vec3 c = mix(c1, c2, fbm(p + r)) + mix(c3, c4, r.y) - mix(c5, c6, r.x);
    float grad = gl_FragCoord.y / resolution.y;

    vec3 result;
    result = vec3(c * cos(shift * gl_FragCoord.y / resolution.y));
    result *= 1.0-grad;

    float alpha = 0;
    if (centerCol.a != 0) {
        alpha = result.r + result.g + result.b + 2.0;
        gl_FragColor = vec4(result, alpha);
    } else {
        alpha = glowShader();
        gl_FragColor = vec4( mix(result, color, 1-alpha), alpha );
    }
}