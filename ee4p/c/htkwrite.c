/* ------< C code to write HTK [1,2].* data >-----
   by Guillaume Gravier <ggravier@inf.enst.fr>
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <Python.h>

/* -------------------------------------------------- */
/* ----- Private type definition for HTKWrite() ----- */
/* -------------------------------------------------- */
typedef struct {
  long nSamples;
  long sampPeriod;
  short sampSize;
  short parmKind;
} htk_header_t;

#define TRUE  1
#define FALSE 0
#define H_USER      9

#define HASENERGY  0100       /* _E log energy included */
#define HASNULLE   0200       /* _N absolute energy suppressed */
#define HASDELTA   0400       /* _D delta coef appended */
#define HASACCS   01000       /* _A acceleration coefs appended */
#define HASCOMPX  02000       /* _C is compressed */
#define HASZEROM  04000       /* _Z zero meaned */
#define HASCRCC  010000       /* _K has CRC check */
#define HASZEROC 020000       /* _0 0'th Cepstra included */
#define HASVQ    040000       /* _V has VQ index attached */
#define HASTHIRD 0100000       /* _T has Delta-Delta-Delta index attached */

static PyObject * HTKWriteBin(PyObject *self, PyObject *args)
{
  htk_header_t header;
  short htk_kind;
  int nelem, i, samps, len, *s;
  float *ptr, period;
  char *filename, *flags, buf[255];
  PyListObject *in_data;
  int hasV=FALSE,hasE=FALSE,hasD=FALSE,hasN=FALSE,hasA=FALSE,hasT=FALSE,hasF=FALSE,hasC=FALSE,hasK=FALSE,hasZ=FALSE,has0=FALSE;

  if (!PyArg_ParseTuple(args, "iifsOs", &nelem, &samps, &period, &flags, &in_data, &filename))
    return Py_BuildValue("i",7);

  header.nSamples=(long)samps; /* set number of samples */
  header.sampSize=(short)(nelem*sizeof(float)); /* set sample size */
  header.sampPeriod=(long)(period*10000.0); /* set sample period */

  htk_kind=(short)H_USER;

  strcpy(buf,flags);
  len=strlen(buf);
  s=&buf[len-2];
  while (len>2 && s=='_') {
    switch(*(s+1)){
    case 'E': hasE = TRUE; break;
    case 'D': hasD = TRUE; break;
    case 'N': hasN = TRUE; break;
    case 'A': hasA = TRUE; break;
    case 'C': hasC = TRUE; break;
    case 'T': hasT = TRUE; break;
    case 'F': hasF = TRUE; break;
    case 'K': hasK = TRUE; break;
    case 'Z': hasZ = TRUE; break;
    case '0': has0 = TRUE; break;
    case 'V': hasV = TRUE; break;
    default: ;;
    }
    *s = '\0';
	len -= 2;
	s -= 2;
  }

	if (hasE) htk_kind |= HASENERGY;
	if (hasD) htk_kind |= HASDELTA;
	if (hasN) htk_kind |= HASNULLE;
	if (hasA) htk_kind |= HASACCS;
	if (hasT) htk_kind |= HASTHIRD;
	if (hasK) htk_kind |= HASCRCC;
	if (hasC) htk_kind |= HASCOMPX;
	if (hasZ) htk_kind |= HASZEROM;
	if (has0) htk_kind |= HASZEROC;
	if (hasV) htk_kind |= HASVQ;
  header.parmKind=htk_kind;

  if(fwrite(&header,sizeof(htk_header_t),1,filename) != 1) {
    printf("HTKWriteFS(): cannot write HTK header (%d bytes)", sizeof(htk_header_t));
    return Py_BuildValue("i",5);
  }
  for(i=0;i<(int)PyList_Size(in_data);i++) {
    ptr=(float *)PyList_GetItem(in_data,i);
	printf("%f\n", ptr);
    /* now, ptr points to the i'th feature vector! Write it! */
    if(fwrite(ptr,sizeof(float),nelem,filename) != nelem) {
      printf("HTKWriteFS(): cannot write %d'th vector (%d bytes)", i+1,nelem*sizeof(float));
      return Py_BuildValue("i",5);
    }
  }

  return Py_BuildValue("i",0);
}

static PyMethodDef HTKWriteMethods[] = {
    {"writebinfile", HTKWriteBin, METH_VARARGS, "Write HTK data out to a binary file"},
    {NULL, NULL, 0, NULL}        /* Sentinel */
};

PyMODINIT_FUNC inithtkwritefile(void) {
    (void) Py_InitModule("htkwritefile", HTKWriteMethods);
}

