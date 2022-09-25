1. install Java 1.8 (or higher) JDK and make sure `java` can be used.
2. install `mvn`.
3. compile: `mvn clean install`.
4. execute: `java -jar target/javasymbolsolver-maven-sample-1.0-SNAPSHOT-shaded.jar `.


在simplified dataset中
1.  Eval: 简化了fully qualified name的atlas/RawDataset
2.  Testing: 同上
3.  Training: 太大了, 不能直接上传. 
4.  simp_csv: 简化后的数据集生成的csv
    1.  old 使用atlas原始的方式
    2.  new 补充了non-typeable-arg类型的assertion生成的candidates

注意: 如果文件内存在'var24678<空格>', 将其替换为''

