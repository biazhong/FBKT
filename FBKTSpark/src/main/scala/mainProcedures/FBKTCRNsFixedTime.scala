package mainProcedures

import rngstream._
import driver._
import problems._
import collection.mutable._;
import collection.JavaConversions._;
import org.apache.spark._;
import org.apache.spark.SparkContext._;
import java.io._;
import scala.util.Random;

object FBKTCRNsFixedTime {
  def withinCoreSelection(coreID: Int, nCores: Int, nSys: Int, T0: Double, T:Double, param: String, acc_sim: Accumulator[Long],acc_sim_t: Accumulator[Long] ):(Int)={
    val FBKT = new FBKTCRNsFixedTimeJ(coreID,nCores,nSys,T0,T,param);
    FBKT.runSystem();
    acc_sim += FBKT.getTotalSampleSize();
    acc_sim_t+=FBKT.getTotalSimTime();
    
    (FBKT.getBestID());
  }
  
  
  def finalSelection(coreID:Int, coreOutPut:Array[Int], TmaxR:Double,param: String, acc_sim: Accumulator[Long],acc_sim_t: Accumulator[Long]):(Array[Double])={
    val FBKT = new FBKTCRNsFixedTimeFinalStepJ(coreOutPut,TmaxR,param);
    FBKT.runSystem();
    acc_sim += FBKT.getTotalSampleSize();
    acc_sim_t+=FBKT.getTotalSimTime();
   
    var results :Array[Double]=FBKT.getResults();
    
    (results);
  }
  
  
  def main(args:Array[String]):Unit={
    
    
    
    
    
    //val outputFile = "/home/puyang/Desktop/output";//args(0);
    val T0 = args(0).toDouble*1000; //in sec
    val T = args(1).toDouble*1000;  //in sec
    val param = args(2);
    val nCores = args(3).toInt;
    val repeat = args(4).toInt;
 
    
    val nSys=problems.TpMax.getNumSystems(param).toInt;
    val maxR = Math.ceil(Math.log(nSys/nCores)/Math.log(2)).toInt + 1;
    
    var outputstring="";
    
    var count =0;
    
    println(nSys);
    println(maxR);
    
    val conf = new SparkConf().setAppName("FBKT-Spark").set("spark.cores.max",nCores.toString());
    val sc = new SparkContext(conf);
    
    while (count < repeat){
      val start_t = System.nanoTime();
      val coreID = sc.parallelize(0 to nCores-1,nCores);
      
      val Allocation = 3;
      val TmaxR = maxR*1.0/(Allocation-1)*Math.pow((Allocation-1.0)/Allocation, maxR)*T/nCores;
    
      
      val accum_sim = sc.accumulator(0L,"Accumulator: total sample size");
      val accum_sim_t = sc.accumulator(0L,"Accumulator: total simulation time");
    
      val coreOutPut = coreID.map(withinCoreSelection(_, nCores, nSys, T0, T, param,accum_sim,accum_sim_t)).cache().collect();
     
      val finalOutPut = coreID.map(finalSelection(_, coreOutPut, TmaxR,param, accum_sim,accum_sim_t)).cache().collect();
      var sum :Array[Double]= new Array[Double](coreOutPut.size);
      
      for(ii <- 0 to finalOutPut.size-1){
        for(jj <- 0 to coreOutPut.size-1){
          sum(jj) = sum(jj) + finalOutPut(ii)(jj);
        }
      }
      var recordMax = -100000d;
      var maxIndex = -1;
      for(ii <- 0 to coreOutPut.size-1){
        if (sum(ii)>recordMax){
          maxIndex = ii;
          recordMax = sum(ii);
        }
      }
      val finalResult = coreOutPut(maxIndex);
      
      val final_t = (System.nanoTime() - start_t).toDouble/1e9;
    
      println(f"Total time = $final_t%.2f secs.");
      println(finalResult);
      println(accum_sim.value);
      println(accum_sim_t.value);
      outputstring += f"Total time = $final_t%.2f secs."+ " "+finalResult+" "+accum_sim.value+" "+accum_sim_t.value+"\n";
      count+=1;
    }
    val pw = new PrintWriter(new File("Output.txt"));
    pw.write(outputstring);
    pw.close();
    sc.stop
  }
}