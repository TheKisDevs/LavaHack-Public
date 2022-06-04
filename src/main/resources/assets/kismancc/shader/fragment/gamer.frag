precision highp float;

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
 return fract(cos(dot(n, vec2(2.9898, 20.1414))) * 5.5453);
}

float noise(vec2 n) {
  const vec2 d = vec2(0.0, 1.0);
  vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
  return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

float fbm(vec2 n){
   float total=0.,amplitude=1.5;
   for(int i=0;i<18;i++){
       total+=noise(n)*amplitude;
        n+=n;
        amplitude*=.45;
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

void main(){
    const vec3 c1=vec3(0.502, 0.1059, 0.1059);
    const vec3 c2=vec3(167./255.,93./255.,110./255.);
    const vec3 c3=vec3(0.4902, 0.5333, 0.4902);
    const vec3 c4=vec3(0.2118, 0.3451, 0.2706);
    const vec3 c5=vec3(0.3176, 0.2549, 0.4);
    const vec3 c6=vec3(0.8, 0.3569, 0.3569);
    
    vec2 p=gl_FragCoord.xy*5./resolution.xx;
    float q=fbm(p-time*.05);
    vec2 r=vec2(fbm(p+q+time*speed.x-p.x-p.y),fbm(p+q-time*speed.y));
    vec3 c=mix(c1,c2,fbm(p+r))+mix(c3,c4,r.x)-mix(c5,c6,r.y);
    float grad=gl_FragCoord.y/resolution.y;

    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);
    vec4 result;

    result = vec4(c*cos(shift*gl_FragCoord.y/resolution.y),1.5);
    result.xyz*=1.15-grad;

    float alpha = 0;
    if (centerCol.a != 0) {
        alpha = calcLuma(result)*15;
        gl_FragColor = vec4(result.rgb, alpha);
    } else {
        alpha = glowShader();
        gl_FragColor = vec4( mix(result, color, 1-alpha)*1.5, alpha );
    }
}