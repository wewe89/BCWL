本目录中的两个jar包，视不同情况同时只需使用其中之一。
feedback-{VERSION}.jar：默认使用的jar包。
feedback _lite-{VERSION}.jar：如果您的应用还有集成其它的百度SDK，有可能出现公用包的重复加载，如com.baidu.android.common.xxx。这种情况下，请使用这个lite版本jar。
