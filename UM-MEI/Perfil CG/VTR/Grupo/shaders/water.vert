#version 440

uniform mat4 projectionViewModel;
uniform float scale;
uniform float firstLayer;
uniform mat4 m_view;
uniform vec4 l_dir;

in vec4 position;
in vec3 normal;

out vec4 fragPos;
out vec3 fragNormal;
out vec3 lightDir;

void main()
{
    lightDir = normalize(vec3(m_view * l_dir));
    vec4 pos = vec4(position.x, firstLayer * scale * 0.8, position.zw);
    fragNormal = normal;
    fragPos = pos;
    gl_Position = projectionViewModel * pos;
}