#include <unistd.h>
#include "json-builder.h"


const char *optstring = "f:s";
const 
/*pci-id-inster.c*/
int main(int argc, char **argv){
    /*Step 1 - check args*/
    char opt;
    while((opt=getopt(argc, argv, optstring)) != -1){
        switch (opt)
        {
        case 'f': /* constant-expression */:
            /* parse json array */
            parse_json_array(optarg)
            
            break;
        
        case 's':
            break;
            optarg;
        default:
            printf(help);
            break;
        }

    }


    /*Step 2 - open file */

    return 0;
}