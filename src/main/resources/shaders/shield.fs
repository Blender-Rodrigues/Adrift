#version 120

uniform vec2 size;
uniform vec2 location;
uniform int intensity;

void main() {

    vec2 loc = (gl_FragCoord.xy - location) / size;
    float pct = 0.0;

    pct = sqrt(loc.x*loc.x + loc.y*loc.y);

    if (pct > 1.0) {
        pct = 0.0;
    }
    pct = pct * intensity / 50;

    gl_FragColor = vec4(0.0, 0.0, 1.0, pct);
}