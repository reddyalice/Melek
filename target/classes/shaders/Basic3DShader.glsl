#shader vertex
#version 400 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec2 pass_texCoords;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;


void main(void){

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    pass_texCoords = texCoords;

}

#shader fragment
#version 400 core

in vec2 pass_texCoords;
out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec4 color;

void main(void){

    out_Color = texture(textureSampler, pass_texCoords) * color;
}