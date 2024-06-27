#version 440

uniform vec4 cam_pos;
uniform float fog_density;
uniform float firstLayer, secondLayer, thirdLayer;
uniform sampler2D texGrass, texSnow, texSand, texRock;
uniform float scale;

in Data {
    vec3 normal;
    vec3 lightDir;
    //vec4 color;
    vec4 vertex;
} DataIn;


out vec4 colorOut;

vec4 fog(vec3 color){
    vec4 fogColor = vec4(0.5, 0.6, 0.7, 1.0);
    float distance = distance(cam_pos, DataIn.vertex);
	float amount = 1.0 - exp(-distance * fog_density);

    return vec4(mix(color, fogColor.xyz, amount), 1.0);
}

vec4 calcColor(vec4 vert){
    float factor = scale * vert.y;
    vec4 colorAux;
    vec2 texCoord = fract(vert.xz);
    if(factor > (thirdLayer * scale)) {
        colorAux = vec4(texture(texSnow, texCoord).xyz, 1.0);
    } else if(factor > (secondLayer * scale)) {
        colorAux = vec4(texture(texRock, texCoord).xyz, 1.0);
    } else if(factor > (firstLayer * scale)) {
        colorAux = vec4(texture(texGrass, texCoord).xyz, 1.0);
    } else {
        colorAux = vec4(texture(texSand, texCoord).xyz, 1.0);
    }
    return colorAux;
}


void main() {
    vec3 n = normalize(DataIn.normal);
    vec3 l = normalize(DataIn.lightDir);
    float intensity = max(dot(n, l), 0.1);

    colorOut = fog(intensity * calcColor(DataIn.vertex).xyz);
}