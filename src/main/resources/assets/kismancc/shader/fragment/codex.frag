#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

// Starry Night by @dorald

uniform float time;
uniform vec2 resolution;
uniform sampler2D texture;

float random(vec2 ab) {
    float f = (cos(dot(ab ,vec2(21.9898,78.233))) * 43758.5453);
    return fract(f);
}

float noise(in vec2 xy) {
    vec2 ij = floor(xy);
    vec2 uv = xy-ij;
    uv = uv*uv*(3.0-2.0*uv);
    
    
    float a = random(vec2(ij.x, ij.y ));
    float b = random(vec2(ij.x+1., ij.y));
    float c = random(vec2(ij.x, ij.y+1.));
    float d = random(vec2(ij.x+1., ij.y+1.));
    float k0 = a;
    float k1 = b-a;
    float k2 = c-a;
    float k3 = a-b-c+d;
    return (k0 + k1*uv.x + k2*uv.y + k3*uv.x*uv.y);
}

float glowShader() {
    float radius = 2.0;
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
    vec2 position = (gl_FragCoord.xy+ - 0.5 * resolution.xy) / resolution.yy;
    
    float color = pow(noise(gl_FragCoord.xy), 25.0) * 30.0; // Change the value here !
    
    float r1 = noise(gl_FragCoord.xy*noise(vec2(sin(time*0.01))));
    float r2 = noise(gl_FragCoord.xy*noise(vec2(cos(time*0.01), sin(time*0.01))));
    float r3 = noise(gl_FragCoord.xy*noise(vec2(sin(time*0.05), cos(time*0.05))));

    float alpha = 0;
    if (centerCol.a != 0) {
        alpha = 1.0;
    } else {
        alpha = glowShader();
    }

    gl_FragColor = vec4(vec3(color*r1, color*r2, color*r3), alpha);
}