#shader vertex
#version 400 core
layout (location=0) in vec3 position;
layout (location=1) in vec3 normals;
layout (location=2) in vec2 textureCoords;

layout (location=3) in vec4 color;
layout (location=4) in float texID;


out vec4 pass_color;
out vec2 pass_texCoords;
out float pass_texId;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;


void main(){

    pass_color = color;
    pass_texCoords = textureCoords;
    pass_texId = texID;

    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);

}

#shader fragment
#version 400 core


in vec4 pass_color;
in vec2 pass_texCoords;
in float pass_texId;
out vec4 out_Color;

uniform sampler2D textureSampler[gl_MaxTextureImageUnits];

void main(){
    int id = int(pass_texId);
    out_Color =  pass_color * texture(textureSampler[id], pass_texCoords);
}