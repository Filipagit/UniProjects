from PIL import Image
import numpy
import sys

def generate_normals(heightmap):
    height, width = heightmap.shape
    normals = numpy.zeros((height, width, 3))
    dx = numpy.gradient(heightmap, axis=1)
    dy = numpy.gradient(heightmap, axis=0)
    for i in range(height):
        for j in range(width):
            normal = numpy.array([-dx[i, j], -dy[i, j], 1])
            normals[i, j] = (normal / numpy.linalg.norm(normal) + 1) / 2
    return normals
            

def main():
    img = Image.open(sys.argv[1]).convert("L") # Convert to grayscale
    heightmap = numpy.array(img)
    # generate normals
    normals = generate_normals(heightmap)
    img_n = Image.fromarray((normals * 255).astype(numpy.uint8))
    img_n.save(sys.argv[2])
    #print(normals)
    return

if __name__ == "__main__":
    main()