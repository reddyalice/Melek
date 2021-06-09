#shader vertex
#version 400 core

in vec2 position;
in vec2 texCoords;

out vec2 pass_texCoords;

uniform mat4 transformationMatrix;
uniform mat4 combinedMatrix;


void main(void){

    vec4 worldPosition = transformationMatrix * vec4(position, 0.0, 1.0);
    gl_Position = combinedMatrix * worldPosition;

    pass_texCoords = texCoords;

}


#shader fragment
#version 400 core

in vec2 pass_texCoords;
out vec4 out_Color;

uniform sampler2D textureSampler;

void main(void){

    out_Color = texture(textureSampler, pass_texCoords);
}