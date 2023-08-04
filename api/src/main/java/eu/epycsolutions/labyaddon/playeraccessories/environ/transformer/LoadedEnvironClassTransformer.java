package eu.epycsolutions.labyaddon.playeraccessories.environ.transformer;

public interface LoadedEnvironClassTransformer {

  void init();

  boolean shouldTransform(String name, String transformedName);

  byte[] transform(String name, String transformedName, byte[] classBytes);

}
