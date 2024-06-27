#version 440

uniform sampler2D texSky;
uniform int skyColorOrTex;
uniform vec4 cam_pos;
uniform float fog_density;

in vec4 pos;
in vec2 texCoord;

out vec4 color;

vec4 fog(vec3 color){
    vec4 fogColor = vec4(0.5, 0.6, 0.7, 1.0);
    float distance = distance(cam_pos, pos);
	float amount = 1.0 - exp(-distance * min(fog_density, 0.01));

    return vec4(mix(color, fogColor.xyz, amount), 1.0);
}

void main() {
    //x="0.8" y="1.0" z="1.0" w="1.0"
    if(skyColorOrTex == 1){
        //color = vec4(texture(texSky, texCoord).rgb, 1.0);
        color = fog(texture(texSky, texCoord).rgb);
    }else{
        //color = vec4(0.8, 1.0, 1.0, 1.0);
        color = fog(vec3(0.8, 1.0, 1.0));
    }
}