#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 tex_coords;

uniform mat4 projection;
uniform mat4 rotation;
uniform mat4 location;
uniform mat4 inverseLocation;

void main() {
    tex_coords = textures;
    gl_Position = projection * vec4(vertices, 1);
    gl_Position = inverseLocation * gl_Position;
    gl_Position = rotation * gl_Position;
    gl_Position = location * gl_Position;
}