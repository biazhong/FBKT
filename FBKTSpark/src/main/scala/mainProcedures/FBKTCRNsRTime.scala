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

object FBKTCRNsRTime {
  def withinCoreSelection(coreID: Int, nCores: Int, nSys: Int, N0: Double, N:Double, param: String, initialSeed:Double, finalSeed: Double, omega:Double,acc_sim_t: Accumulator[Long] ):(Int,Double)={
    val FBKT = new FBKTCRNsRTimeJ(coreID,nCores,nSys,N0,N,param,initialSeed,finalSeed,omega);
    FBKT.runSystem();
    acc_sim_t += FBKT.getSimTime();
    
    (FBKT.getBestID(),FBKT.getBestIDSampleAverage());
  }
  
  
  
  def main(args:Array[String]):Unit={
    
    
    
    
    
    //val outputFile = "/home/puyang/Desktop/output";//args(0);
    val n0 = args(0).toDouble;
    val n = args(1).toDouble;
    val omega = args(2).toDouble;
    val param = args(3);
    val nCores = args(4).toInt;
    val repeat = args(5).toInt;
    val seed = args(6).toInt;
    
    val nSys=problems.TpMax.getNumSystems(param).toInt;
    val maxR = Math.ceil(Math.log(nSys/nCores)/Math.log(2)).toInt + 1;
    
    val N0 = nSys * n0;
    val N = nSys * n;
    var outputstring="";
    
    var count =0;
    
    println(nSys);
    println(maxR);
    
    val conf = new SparkConf().setAppName("FBKT-Spark").set("spark.cores.max",nCores.toString());
    val sc = new SparkContext(conf);
    val R = new Random(seed);
    while (count < repeat){
      val start_t = System.nanoTime();
      val coreID = sc.parallelize(0 to nCores-1,nCores);
    
    
      val accum_sim_t = sc.accumulator(0L,"Accumulator: total simulation time");
    
      val initialSeed= Math.ceil(R.nextDouble()*10000);
      val seedFinal = Math.ceil(R.nextDouble()*10000);
    
      val coreOutPut = coreID.map(withinCoreSelection(_, nCores, nSys, N0, N, param,initialSeed,seedFinal,omega,accum_sim_t)).cache();

      val finalOutPut = coreOutPut.reduce((x,y)=>if(x._2>y._2) x else y);
      val final_t = (System.nanoTime() - start_t).toDouble/1e9;
      //println(f"Total time = $final_t%.2f secs.");
      //println(finalOutPut._1);
      //println(accum_sim_t.value);
      outputstring += f"Total time = $final_t%.2f secs."+ " "+finalOutPut._1+" "+accum_sim_t.value+"\n";
      count+=1;
    }
    val pw = new PrintWriter(new File("Output.txt"));
    pw.write(outputstring);
    pw.close();
    sc.stop
  }
}