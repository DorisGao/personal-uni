#!c:/Program Files/Perl5.6.1/bin/perl

#####################################################################################################
# 
# This script runs testing
# Usage:  perl ASR_EE4H_onlyTest_3mix.pl 
# Author: Munevver Kokuer
# Date: Nov. 2008
#####################################################################################################


# Global Variables

$REC_DIR    	= "D:\\LabASR";
#$BINDIR     	= "C:\\HTK\\HTK3.2bin";
$BINDIR     	= "$REC_DIR\\binHTK3.2"; 

$HMM_DIR    	= "$REC_DIR\\hmmsTrained";

$LIST_TEST	= "$REC_DIR/list/listTestFulPath.scp"; 
$LIST_Test_Hcopy= "$REC_DIR/list/listTestFulPath_Hcopy.scp";

$CONFIG_test   	= "$REC_DIR/config/config_test_mfcc";

$WORD_LISTSP	= "$REC_DIR/lib/wordList_withSP";

$LABELSP    	= "$REC_DIR/label/label_EE4H_withSP.mlf";

$mod_file 	= "$HMM_DIR/hmm20/models";
$mac_file 	= "$HMM_DIR/hmm20/macros"; 

$flags      	= "-p 10 -s 0.0";
$NET        	= "$REC_DIR\\lib\\wordNetwork";
$DICT       	= "$REC_DIR\\lib\\wordDict"; 


$RESULT 	= "$REC_DIR\\result\\recognitionFinalResult.res";


open(STDOUT, ">$REC_DIR\\result\\LogASR_EE4H_onlyTesting.log") or die "Can't write to STDOUT $!";

#-------------------------------------------------------------------------
# Testing 
#-------------------------------------------------------------------------

print "Testing...\n";

system "$BINDIR/HVite -H $mac_file -H $mod_file -S $LIST_TEST -C $CONFIG_test -w $NET -i $REC_DIR/result/result.mlf $flags $DICT $WORD_LISTSP";

system ("$BINDIR/HResults -e \"???\" sil -e \"???\" sp -I $LABELSP $WORD_LISTSP $REC_DIR/result/result.mlf >> $RESULT "); 

print "Testing complete\n";
print("\n------------------------------------------------------------------\n");


#--------------------------------------------------------------------------#
#                   End of Script: ASR_EE4H_onlyTest_3mix.pl               #
#--------------------------------------------------------------------------#