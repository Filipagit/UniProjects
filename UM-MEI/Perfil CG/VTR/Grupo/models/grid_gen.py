import sys
from PIL import Image
import numpy

def generate_grid(height, width, y, divx, divz):
    vertices = []
    faces = []

    for x in range(divx):
        for z in range(divz):
            vertex_x = (x / divx) * height
            vertex_z = (z / divz) * width
            vertices.append((vertex_x, y, vertex_z))

    for x in range(divx):
        for z in range(divz):
            i = x * (divx + 1) + z + 1
            faces.append((i, i + 1, i + divz + 2))
            faces.append((i, i + divz + 2, i + divz + 1))

    return vertices, faces


# def generate_plain_vertex(height, width, y):
#     vertex = [(-height / 2, (0 if y is None else y), -width / 2), (-height / 2, (0 if y is None else y), width / 2),
#               (height / 2, (0 if y is None else y), width / 2), (-height / 2, (0 if y is None else y), width / 2)]
    
#     faces = []
#     faces.append((1, 2, 4))
#     faces.append((1, 4, 3))

#     return vertex, faces


def gen_obj(vertex, faces, normals, filename):
    with open(filename, "w") as f:
        for v in vertex:
            f.write(f"v {v[0]} {v[1]} {v[2]}\n")

        for n in normals:
            for a in n:
                f.write(f"vn {a[0]/255} {a[1]/255} {a[2]/255}\n")

        for i in range(0, len(faces)):
            f.write(f"f {faces[i][0]}//{faces[i][0]} {faces[i][1]}//{faces[i][1]} {faces[i][2]}//{faces[i][2]}\n")
    f.close()


def main():
    nFile = sys.argv[1]
    
    h = int(sys.argv[2])
    w = int(sys.argv[3])
    y = int(sys.argv[4])
    
    filename = sys.argv[5]
    img = Image.open(nFile).convert("RGB")
    nMap = numpy.array(img)
    height, width, _ = nMap.shape
    
    
    vertex, faces = generate_grid(h, w, y, width, height)
    gen_obj(vertex, faces, nMap, filename)
    


if __name__ == "__main__":
    main()
