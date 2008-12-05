/* ------< C code to write HTK [1,2].* data >-----
   by Guillaume Gravier <ggravier@inf.enst.fr>
*/

/* -------------------------------------------------- */
/* ----- Private type definition for HTKWrite() ----- */
/* -------------------------------------------------- */
typedef struct {
  long nSamples;
  long sampPeriod;
  short sampSize;
  short parmKind;
} htk_header_t;

#define H_LPC       1
#define H_LPREFC    2
#define H_LPCEPSTRA 3
#define H_MFCC      6
#define H_FBANK     7
#define H_USER      9

#define HASENERGY   0x0040
#define HASNOE      0x0080
#define HASDELTA    0x0100
#define HASZMEAN    0x0800

/* ----------------------------------------------------- */
/* ----- err_t HTKWrite(data_t *,float,int,FILE *) ----- */
/* ----------------------------------------------------- */
/*
 * Write data (structure containing feature vectors) to output
 * stream in HTK 1.* or 2.* format (HTK 3.0 format is the same
 * as the HTK 2.* format).
 */
err_t HTKWrite(data_t *data,float period,int hversion,FILE *f)
{
  htk_header_t header;
  short htk_kind;
  int nelem;
  float *ptr;
  int i;

  if(sizeof(param_t) != sizeof(float)) {
    /*
      if param_t is double, should copy to a vector of float
      since HTK needs float, not double
    */
    gerror(HCOMPAT_ERR,"HTKWrite(): sizeof(param_t) != sizeof(float)");
    return(HCOMPAT_ERR);
  }

  nelem=DataVecSize(data); /* set feature vector dimension */
  header.nSamples=(long)(data->n); /* set number of samples */
  header.sampSize=(short)(nelem*sizeof(float)); /* set sample size */
  header.sampPeriod=(long)(period*10000.0); /* set sample period */

  if(hversion == 1) /* HTK 1.* format --> try to find a sample kind equivalence */
    switch(data->kind) {
    case FBANK: htk_kind=(short)H_FBANK; break;
    case FBCEPSTRA: htk_kind=(short)H_MFCC; break;
    case LPCEPSTRA: htk_kind=(short)H_LPCEPSTRA; break;
    case LPCOEFF: htk_kind=(short)H_LPC; break;
    case PARCOR: htk_kind=(short)H_LPREFC; break;
    default:
      gerror(NO_HFORMAT_ERR,"HTKWrite(): No equivalent format in HTK 1.4 for %s",
             kind2str(data->kind));
      return(NO_HFORMAT_ERR);
    }
  else /* HTK 2.* --> write USER defined data */
    htk_kind=(short)H_USER;

  if(data->flag & WITHE) htk_kind |= HASENERGY; /* set header flags */
  if(data->flag & WITHD) htk_kind |= HASDELTA;
  if(data->flag & WITHN) htk_kind |= HASNOE;
  if(hversion == 2 && (data->flag & WITHZ)) htk_kind |= HASZMEAN;
  header.parmKind=htk_kind;

  if(fwrite(&header,sizeof(htk_header_t),1,f) != 1) {
    gerror(DUMP_ERR,"HTKWrite(): cannot write HTK header (%d bytes)",
           sizeof(htk_header_t));
    return(DUMP_ERR);
  }
  for(i=0;i<data->n;i++) {
    ptr=(float *)DataGetVec(data,i,nelem);
    /* now, ptr points to the i'th feature vector! Write it! */
    if(fwrite(ptr,sizeof(float),nelem,f) != nelem) {
      gerror(DUMP_ERR,"HTKWrite(): cannot write %d'th vector (%d bytes)",
             i+1,nelem*sizeof(float));
      return(DUMP_ERR);
    }
  }

  DONE;
}
