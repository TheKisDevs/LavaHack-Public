#ifdef GL_ES
precision mediump float;
#endif

#define TAU 6.28318530718
#define MAX_ITER 5

#extension GL_OES_standard_derivatives : enable

uniform sampler2D texture;

uniform float time;
uniform vec2 resolution;

void main() {

	vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    vec2 st = gl_FragCoord.xy/resolution.xy;
    st.x *= resolution.x/resolution.y;

    vec3 color = vec3(0.727,0.221,0.765);
    color = vec3(st.x,st.y,abs(sin(time)));

	gl_FragColor = vec4(color, centerCol.a);
}