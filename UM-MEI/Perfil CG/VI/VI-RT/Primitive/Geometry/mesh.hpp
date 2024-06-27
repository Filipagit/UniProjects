//
//  mesh.hpp
//  VI-RT
//
//  Created by Luis Paulo Santos on 05/02/2023.
//

#ifndef mesh_hpp
#define mesh_hpp

#include "geometry.hpp"
#include "../../utils/vector.hpp"
#include <vector>

// partially inspired in pbrt book (3rd ed.), sec 3.6, pag 152

typedef struct Face {
    int vert_ndx[3];            // indices to our internal vector of vertices (in Mesh)
    Vector geoNormal;           // geometric normal
    bool hasShadingNormals;     // are there per vertex shading normals ??
    int vert_normals_ndx[3];    // indices to veritices normals
    Vector edge1, edge2; //PODE TER ERRO
    int FaceID; //PODE TER ERRO
    BB bb;      // face bounding box
                // this is min={0.,0.,0.} , max={0.,0.,0.} due to the Point constructor
} Face;

class Mesh: public Geometry {
private:
    bool TriangleIntersect (Ray r, Face f, Intersection *isect);
    void GetUVs(Point uv[3]);
public:
    int numFaces;
    std::vector<Face> faces;
    int numVertices;
    std::vector<Point> vertices;
    int numNormals;
    std::vector<Vector> normals;
    bool intersect (Ray r, Intersection *isect);
    void addVertice(Point p);

    int getIndexVertices(Point K);
    
    Mesh(): numFaces(0), numVertices(0), numNormals(0) {
        //this->bb.min = Point(MAXFLOAT,MAXFLOAT,MAXFLOAT);
        //this->bb.max = Point(-MAXFLOAT,-MAXFLOAT,-MAXFLOAT);
    }
};

#endif /* mesh_hpp */