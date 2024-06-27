#version 440

uniform int showWater;
uniform vec4 cam_pos;
uniform float fog_density;

in vec4 fragPos;
in vec3 fragNormal;
in vec3 lightDir;


out vec4 color;

vec4 fog(vec3 color){
    vec4 fogColor = vec4(0.5, 0.6, 0.7, 1.0);
    float distance = distance(cam_pos, fragPos);
	float amount = 1.0 - exp(-distance * fog_density);

    return vec4(mix(color, fogColor.xyz, amount), 1.0);
}

void main() {
    float alpha = 0.55;
    vec3 n = normalize(fragNormal);
    vec3 l = normalize(lightDir);
    float intensity = max(dot(n, l), 0.5);

    vec4 c;
    if (showWater == 1){
        c = vec4(fog(vec3(0.13, 0.53, 0.85) * intensity).xyz, alpha);
    }else{
        c = vec4(0.0, 0.0, 0.0, 0.0);
    }
    
    color = c ;
}