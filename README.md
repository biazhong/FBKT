# FBKT

File Numerical Results.xlsx contains all the numerical results for the paper ``Solving Large-Scale Ranking and Selection Problems''. Notice that the pseudo random number seed used to conduct each numercial experiment is specified in the file.

In the paper, we use Spark Release 2.4.4 and Scala 2.11.12 to conduct the experiments related to the parallel computing.

Folder FBKT contains the code for the first two numerical experiments of the paper.

---File FBKTGeneral.java implements the FBKT procedure; 

---File FBKTSMergeSortGeneral.java implements the FBKT^s procedure; 
  
---File EA.java implements the equal allocation  procedure;
  
---File OCBA.java implements the OCBA procedure.
  
Folder FBKTSpark contains the code for the experiments conducted in section 5.3 of the paper.
  
---File EACRNs.scala implements the EA procedure with CRNs;
  
---File EAnoCRNs.scala implements the EA procedure with no CRNs;
  
---File FBKTCRNs.scala implements the FBKT^s+ procedure with CRNs;
  
---File FBKTnoCRNs.scala implements the FBKT^s+ procedure with no CRNS;
  
---File FBKTCRNsRTime.scala implements the FBKT^s+ procedure used to conduct the numerical experiments for Table 5.

