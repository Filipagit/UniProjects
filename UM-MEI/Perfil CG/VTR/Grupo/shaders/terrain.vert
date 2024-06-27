#version 440

uniform mat4 m_view;
uniform vec4 l_dir;

in vec4 position;

out Data {
	vec3 lightDir;
	vec4 positionV;
} DataOut;

void main() {
    DataOut.lightDir = normalize(vec3(m_view * l_dir));
    DataOut.positionV = position;
    
}