#!c:/Program Files/Perl5.6.1/bin/perl

#####################################################################################################
# 
# This script extract features from Aurora2 'Train clean' data and performs training and testing.
# Usage:  perl ASR_EE4H.pl 
# Author: Munevver Kokuer
# Date: Nov. 2008
#####################################################################################################


# Global Variables

$REC_DIR    	= "D:\\LabASR";
#$BINDIR     	= "C:\\HTK\\HTK3.2bin";
$BINDIR     	= "$REC_DIR\\binHTK3.2"; 

$HMM_DIR    	= "$REC_DIR\\hmmsTrained";

$LIST_TRAIN	= "$REC_DIR/list/listTrainFulPath.scp"; 
$LIST_Train_Hcopy  = "$REC_DIR/list/listTrainFulPath_Hcopy.scp"; 

$LIST_TEST	= "$REC_DIR/list/listTestFulPath.scp"; 
$LIST_Test_Hcopy= "$REC_DIR/list/listTestFulPath_Hcopy.scp";

$CONFIG_HCopy   = "$REC_DIR/config/config_HCopy";
$CONFIG_train   = "$REC_DIR/config/config_train_mfcc";
$CONFIG_test   	= "$REC_DIR/config/config_test_mfcc";

$WORD_LISTSP	= "$REC_DIR/lib/wordList_withSP";
$WORD_LIST  	= "$REC_DIR/lib/wordList_noSP";
$LABELSP    	= "$REC_DIR/label/label_EE4H_withSP.mlf";
$LABELS    	= "$REC_DIR/label/label_EE4H_noSP.mlf"; 

$ED_CMDFILE1	= "$REC_DIR/lib/tieSILandSP_EE4H.hed";

$Proto      	= "$REC_DIR/lib/proto_s1d39_st8m1_EE4H";
$mod_file 	= "$HMM_DIR/hmm8/models";
$mac_file 	= "$HMM_DIR/hmm8/macros"; 

$flags      	= "-p 10 -s 0.0";
$NET        	= "$REC_DIR\\lib\\wordNetwork";
$DICT       	= "$REC_DIR\\lib\\wordDict"; 

$NUM_COEF   	= 39; 
$PAR_TYPE   	= "MFCC_E_D_A";

$RESULT 	= "$REC_DIR\\result\\recognitionFinalResult.res";


open(STDOUT, ">$REC_DIR\\result\\LogASR_EE4H.log") or die "Can't write to STDOUT $!";


#-------------------------------------------------------------------------
# Feature Extraction for mfcc_e_d_a
# HCopy: Calls HCopy to convert AURORA TESTA data files to HTK parameterised 'mfcc_e_d_a'
#-------------------------------------------------------------------------

print "Coding data\n";

system("$BINDIR/HCopy -C $CONFIG_HCopy -S $LIST_Train_Hcopy");
system("$BINDIR/HCopy -C $CONFIG_HCopy -S $LIST_Test_Hcopy");

print "Coding complete\n";

#-------------------------------------------------------------------------
# Training 
#-------------------------------------------------------------------------

print "Training...\n";

foreach $i (0..20) {
    mkdir("$HMM_DIR\\hmm$i");
}

system "$BINDIR\\HCompV -C $CONFIG_train -o hmmdef -f 0.01 -m -S $LIST_TRAIN -M $HMM_DIR/hmm0 $Proto";

print("Seed Hmm succesfully produced\n"); 

system "$BINDIR/macro $NUM_COEF $PAR_TYPE $HMM_DIR/hmm0/vFloors $HMM_DIR/hmm0/macros";
### creates the file "models" containing the HMM definition of all 11 digits and the silence model
system "$BINDIR/models_1mixsil $HMM_DIR/hmm0/hmmdef $HMM_DIR/hmm0/models";

###Training 
foreach $i (1..3) {
	print "Iteration Number $i\n";
	$j=$i-1;
	system "$BINDIR/HERest -D -C $CONFIG_train -I $LABELS -t 250.0 150.0 1000.0 -S $LIST_TRAIN -H $HMM_DIR/hmm$j/macros -H $HMM_DIR/hmm$j/models -M $HMM_DIR/hmm$i $WORD_LIST";
}
print "3 Iterations completed\n";

system("copy $HMM_DIR\\hmm3 $HMM_DIR\\hmm4");
system "$BINDIR/spmodel_gen $HMM_DIR/hmm3/models $HMM_DIR/hmm4/models";
system "$BINDIR/HHEd -T 2 -H $HMM_DIR/hmm4/macros -H $HMM_DIR/hmm4/models -M $HMM_DIR/hmm5 $ED_CMDFILE1 $WORD_LISTSP";
print "SP model fixed\n";

foreach $i (6..8) {
	$j=$i-1;
	system "$BINDIR/HERest -C $CONFIG_train -I $LABELSP -S $LIST_TRAIN -H $HMM_DIR/hmm$j/macros -H $HMM_DIR/hmm$j/models -M $HMM_DIR/hmm$i $WORD_LISTSP";
}
print "6 Iterations completed\n";

print "Training complete\n";


#-------------------------------------------------------------------------
# Testing 
#-------------------------------------------------------------------------

print "Testing...\n";

system "$BINDIR/HVite -H $mac_file -H $mod_file -S $LIST_TEST -C $CONFIG_test -w $NET -i $REC_DIR/result/result.mlf $flags $DICT $WORD_LISTSP";

system ("$BINDIR/HResults -e \"???\" sil -e \"???\" sp -I $LABELSP $WORD_LISTSP $REC_DIR/result/result.mlf >> $RESULT "); 

print "Testing complete\n";
print("\n------------------------------------------------------------------\n");


#--------------------------------------------------------------------------#
#                   End of Script: ASR_EE4H.pl 	                           #
#--------------------------------------------------------------------------#