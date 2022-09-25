1. install Java 1.8 (or higher) JDK and make sure `java` can be used.
2. install `mvn`.
3. compile: `mvn clean install`.
4. execute: `java -jar target/javasymbolsolver-maven-sample-1.0-SNAPSHOT-shaded.jar `.


在simplified dataset中
1.  Eval: 简化了fully qualified name的atlas/RawDataset
2.  Testing: 同上
3.  Training: ~~太大了, 不能直接上传.~~ 已上传
4.  simp_csv: 简化后的数据集生成的csv
    1.  old 使用atlas原始的方式
    2.  new 补充了non-typeable-arg类型的assertion生成的candidates

注意: 如果文件内存在'var24678<空格>', 将其替换为''

经过toga处理的数据集后缀是.csv, 在google drive中的simp_csv
https://drive.google.com/file/d/1GhHX5xBeyVe-MGePtiNKcvgOScqvg3ew/view?usp=sharing

未经toga处理的, 简化了fully qualified name的atlas数据集在仓库的simplified_dataset/中, 以txt为后缀.
其中为了方便toga后续的处理, 没有去掉assertLines.txt中的```org . junit . Assert . ```, 其余部分是正常简化过的. 

 