module minichain {
	requires java.sql;
	requires gson;
	requires org.bouncycastle.provider;

	exports minichain;

	opens minichain;
}