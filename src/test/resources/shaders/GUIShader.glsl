#shader vertex
#version 400 core
in vec2 position;
in vec2 texCoords;
out vec2 pass_texCoords;
uniform vec2 elementPosition;
uniform vec2 elementSize;
uniform vec2 screenSize;
uniform float rotation;
uniform vec2 textureScale;
uniform vec2 textureOffset;
void main(void){
    float angle = radians(rotation);
    float cs = cos(angle);
    float sn = sin(angle);
    vec2 worldPosition = position;
    worldPosition.x = worldPosition.x / screenSize.x * elementSize.x;
    worldPosition.y = worldPosition.y / screenSize.y * elementSize.y;
    worldPosition.x = worldPosition.x * cs - worldPosition.y * sn;
    worldPosition.y = worldPosition.x * sn + worldPosition.y * cs;
    worldPosition += elementPosition / screenSize;
    gl_Position =  vec4(worldPosition, 0.0, 1.0);
    vec2 finalTexCoords = texCoords;
    finalTexCoords.x = texCoords.x / textureScale.x + textureOffset.x;
    finalTexCoords.y = texCoords.y / textureScale.y + textureOffset.y;
    pass_texCoords = finalTexCoords;
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