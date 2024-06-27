#version 440

uniform mat4 m_pvm;
uniform vec4 cam_pos;

in vec4 position;
in vec2 texCoord0;

out vec4 pos;
out vec2 texCoord;

void main() {
    texCoord = texCoord0;
    pos = position;
    gl_Position = m_pvm * vec4(vec3(position), 1.0);
}