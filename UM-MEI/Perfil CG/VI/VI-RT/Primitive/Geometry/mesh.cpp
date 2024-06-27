//
//  mesh.cpp
//  VI-RT
//
//  Created by Luis Paulo Santos on 05/02/2023.
//

#include "mesh.hpp"

// see pbrt book (3rd ed.), sec 3.6.2, pag 157
//
// Suggestion: use:
// // https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
// Moller Trumbore intersection algorithm

bool Mesh::TriangleIntersect (Ray r, Face f, Intersection *isect) {


    bool intersected = f.bb.intersect(r);
    if (!intersected) return false;

    const float EPSILON = 0.0000001;
    Point vertex0 = this->vertices[f.vert_ndx[0]];
    Point vertex1 = this->vertices[f.vert_ndx[1]];
    Point vertex2 = this->vertices[f.vert_ndx[2]];

    Vector edge1, edge2, h, s, q;
    float a,i,u,v;

    edge1 = vertex0.vec2point(vertex1);
    edge2 = vertex0.vec2point(vertex2);

    h = r.dir.cross(edge2);
    a = edge1.dot(h);
    if (a > -EPSILON && a < EPSILON)
        return false;    // This ray is parallel to this triangle.
    i = 1.0f/a;
    Point ps = r.o - vertex0;
    s = Vector(ps.X,ps.Y,ps.Z);
    u = i * s.dot(h);
    if (u < 0.0 || u > 1.0)
        return false;
    q = s.cross(edge1);
    v = i * r.dir.dot(q);
    if (v < 0.0 || u + v > 1.0)
        return false;
    // At this stage we can compute t to find out where the intersection point is on the line.
    float t = i * edge2.dot(q);
    if (t > EPSILON) // ray intersection
    {
        isect->p = r.o + (r.dir * t);



        Vector normal = f.geoNormal;
        Vector wo = -1.f * r.dir;
        // make sure the normal points to the same side of the surface as wo
        normal.Faceforward(wo);
        isect->gn = normal;
        isect->sn = normal;

        isect->depth = t;
        // preencher shading
        isect->wo = Vector(-r.dir.X,-r.dir.Y,-r.dir.Z);
        return true;
    }
    else {// This means that there is a line intersection but not a ray intersection.
        return false;
    }
}


bool Mesh::intersect (Ray r, Intersection *isect) {
    bool intersect = true, intersect_this_face;
    Intersection min_isect, curr_isect;
    float min_depth=MAXFLOAT;
    // intersect the ray with the mesh BB
    if (!bb.intersect(r)) return false;

    // If it intersects then loop through the faces
    intersect = false;
    int index = 0;
    for (auto face_it=faces.begin() ; face_it != faces.end() ; face_it++) {
        intersect_this_face = TriangleIntersect(r, *face_it, &curr_isect);
        if (!intersect_this_face) continue;
        min_isect.FaceID = index;
        intersect = true;
        if (curr_isect.depth < min_depth) {  // this is closer
            min_depth = curr_isect.depth;
            min_isect = curr_isect;
            min_isect.FaceID = index;
        }
        index ++;
    }
    *isect = min_isect;
    return intersect;
}

int Mesh::getIndexVertices(Point K){
    int index =0;
    for (auto it = vertices.begin(); it != vertices.end(); it++){
        if (it->equals(K)) {
            return index;
        }
        index++;
    }
    return -1;
}

void Mesh::addVertice(Point p){
    this->bb.update(p);
    vertices.push_back(p);
    numVertices++;
}