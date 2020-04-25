#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 tex_coords;

uniform mat4 projection;
uniform mat4 rotation;

void main() {
    tex_coords = textures;
    gl_Position = vec4(vertices, 1);
    gl_Position = rotation * gl_Position;
    gl_Position = projection * gl_Position;
}