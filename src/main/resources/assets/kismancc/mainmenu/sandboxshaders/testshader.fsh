#extension GL_OES_standard_derivatives : enable
//r33v01v3
precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

vec3 hsv2rgb (vec3 hsv) {
    hsv.yz = clamp (hsv.yz, 0.0, 1.0);
    return hsv.z * (1.0 + 0.63 * hsv.y * (cos (2.0 * 3.14159 * (hsv.x + vec3 (0.0, 2.0 / 3.0, 1.0 / 3.0))) - 1.0));
}

float world_window(vec2 p,vec2 b){
    vec2 u = abs(p)-b;
    return length(max(u,0.))+min(max(u.x,u.y),0.);
}

void main( void ) {
    vec2 uv = ( gl_FragCoord.xy -.5* resolution.xy )/resolution.y;
    vec3 col = vec3(0.);
    float t = time*0.02;
    uv *= 10.0;

    float window;
    float layers = 80.0;

    float d = 2.5*(sin(t) + sin(t*5.5)+sin(t * 2.0));
    vec2 anchor = vec2(6.2*sin(d),8.6*cos(d));

    for(float i = 0.0; i < 80.0; i++){
        float z = fract(i/layers+t);
        vec2 mid = vec2(6.0*sin(t*10.0),3.0*cos(t*5.0));
        vec2 cam = mix(anchor, mid, z);
        vec2 pos = cam - uv;
        float size = mix(1.0,0.0,z);
        window = world_window(pos*size,vec2(0.1,0.01));
        window = smoothstep(.15,.2,window);
        col += window*(1.-window)*hsv2rgb(vec3(window+t,1.0,z));

    }

    gl_FragColor = vec4(col, 1.0 );

}