
////////////////////////////////////////////
////////////////////////////////////////////
////////////////////////////////////////////
////////////////////////////////////////////    TONEMAP ORIGINAL DO PROFESSOR PARA COMPARAR COM O TONEMAP QUE IMPLEMENTAMOS DO UNCHARTED 2
////////////////////////////////////////////
////////////////////////////////////////////
////////////////////////////////////////////
#include "ImagePPM_original.hpp"
#include <iostream>
#include <fstream>

void ImagePPM_original::ToneMap() {
    imageToSave = new PPM_pixel[W*H];
    
    // loop over each pixel in the image, clamp and convert to byte format
    for (int j = 0 ; j< H ; j++) {
        for (int i = 0; i < W ; ++i) {
            imageToSave[j*W+i].val[0] = (unsigned char)(std::min(1.f, imagePlane[j*W+i].R) * 255);
            imageToSave[j*W+i].val[1] = (unsigned char)(std::min(1.f, imagePlane[j*W+i].G) * 255);
            imageToSave[j*W+i].val[2] = (unsigned char)(std::min(1.f, imagePlane[j*W+i].B) * 255);
        }
    }
}

ImagePPM_original::ImagePPM_original(Image &img) {
    W = img.getW();
    H = img.getH();
    imagePlane = new RGB[W*H];
    imagePlane = img.getImagePlane();
}

bool ImagePPM_original::Save(std::string filename) {
    // convert from float to {0,1,..., 255}
    ToneMap();
    printf("ToneMap\n");

    // write imageToSave to file
    if (this->W == 0 || this->H == 0) {
        fprintf(stderr, "Can't save an empty image\n");
        return false;
    }

    std::ofstream ofs;
    try {
        ofs.open(filename, std::ios::binary);
        if (ofs.fail()) throw("Can't open output file");

        ofs << "P6\n" << this->W << " " << this->H << "\n255\n";
        unsigned char r, g, b;

        // loop over each pixel in the image, clamp and convert to byte format
        for (int i = 0; i < this->W * this->H; ++i) {
            r = imageToSave[i].val[0];
            g = imageToSave[i].val[1];
            b = imageToSave[i].val[2];
            ofs << r << g << b;
        }
        ofs.close();
        return true;
    }
    catch (const char *err) {
        fprintf(stderr, "%s\n", err);
        ofs.close();
        return false;
    }
}
