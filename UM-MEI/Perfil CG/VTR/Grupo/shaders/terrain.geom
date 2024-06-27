#version 440

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

uniform mat4 m_pvm;
uniform mat3 m_normal;
uniform float scale;
uniform float amplitude;
uniform float frequency;
uniform float gain;
uniform float lacunarity;
uniform float min_h;
uniform int octaves;
uniform float noiseSeed;

in Data {
    vec3 lightDir;
    vec4 positionV;
} DataIn[3];

out Data {
    vec3 normal;
    vec3 lightDir;
    vec4 vertex;
} DataOut;

float random(in vec2 st) {
    return fract(sin(dot(st.xy, vec2(127.1, 311.7) + noiseSeed)) * 43758.5453123);
}

float noise(in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float ridgenoise(in vec2 st) {
    float n = noise(st);
    return 2.0 * (0.5 - abs(0.5 - n));
}

float fbm(in vec2 xz) {
    float value = min_h;
    float amp = amplitude;
    float freq = frequency;
    float ridgescale = 1.0;

    for(int i = 0; i < octaves; i++) {
        value += ridgescale * amp * ridgenoise(xz * freq);
        ridgescale *= gain;
        freq *= lacunarity;
    }

    return value;
}

void main() {
    vec4 v[3];
    float h[3];

    for(int i = 0; i < 3; i++) {
        h[i] = fbm(DataIn[i].positionV.xz);
        float factor = scale * h[i];
        v[i] = DataIn[i].positionV + vec4(0.0, factor, 0.0, 0.0);
    }

    vec3 U = v[1].xyz - v[0].xyz;
    vec3 V = v[2].xyz - v[0].xyz;
    vec3 triangleNormal = normalize(cross(U, V));

    for(int i = 0; i < 3; i++) {
        DataOut.normal = normalize(m_normal * triangleNormal);
        DataOut.lightDir = DataIn[i].lightDir;
        DataOut.vertex = v[i];
        gl_Position = m_pvm * v[i];
        EmitVertex();
    }
    EndPrimitive();
}
