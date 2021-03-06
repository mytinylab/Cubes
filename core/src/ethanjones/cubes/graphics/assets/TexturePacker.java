package ethanjones.cubes.graphics.assets;

import com.badlogic.gdx.graphics.Pixmap;

import java.util.HashMap;
import java.util.Map;

public class TexturePacker {

  public static final class PackRectangle {

    int x;
    int y;
    int width;
    int height;

    public PackRectangle() {
      this(0, 0, 0, 0);
    }

    public PackRectangle(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    public PackRectangle(PackRectangle packRectangle) {
      this(packRectangle.x, packRectangle.y, packRectangle.width, packRectangle.height);
    }
  }

  static final class Node {

    public Node leftChild;
    public Node rightChild;
    public PackRectangle rect;
    public String name;

    public Node(int x, int y, int width, int height, Node leftChild, Node rightChild, String name) {
      this.rect = new PackRectangle(x, y, width, height);
      this.leftChild = leftChild;
      this.rightChild = rightChild;
      this.name = name;
    }

    public Node() {
      rect = new PackRectangle();
    }
  }

  Pixmap pixmap;
  int padding;
  boolean duplicateBorder;
  Node root;
  Map<Asset, PackRectangle> rectangles;

  public TexturePacker(int width, int height, int padding, boolean duplicateBorder) {
    this.pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
    this.padding = padding;
    this.duplicateBorder = duplicateBorder;
    this.root = new Node(0, 0, width, height, null, null, null);
    this.rectangles = new HashMap<Asset, PackRectangle>();
  }

  public boolean insertImage(Asset asset, Pixmap image) {
    if (rectangles.containsKey(asset)) throw new RuntimeException("Key \"" + asset.toString() + "\" is already in map");

    int borderPixels = padding;
    borderPixels <<= 1;
    PackRectangle rect = new PackRectangle(0, 0, image.getWidth() + borderPixels, image.getHeight() + borderPixels);
    Node node = insert(root, rect);

    if (node == null) return false;

    node.name = asset.toString();
    rect = new PackRectangle(node.rect);
    rect.width -= borderPixels;
    rect.height -= borderPixels;
    borderPixels >>= 1;
    rect.x += borderPixels;
    rect.y += borderPixels;
    rectangles.put(asset, rect);
    pixmap.drawPixmap(image, rect.x, rect.y);

    if (duplicateBorder) {
      pixmap.drawPixmap(image, rect.x, rect.y - 1, 0, 0, image.getWidth(), 1);
      pixmap.drawPixmap(image, rect.x, rect.y + image.getHeight(), 0, image.getHeight() - 1, image.getWidth(), 1);
      pixmap.drawPixmap(image, rect.x - 1, rect.y, 0, 0, 1, image.getHeight());
      pixmap.drawPixmap(image, rect.x + image.getWidth(), rect.y, image.getWidth() - 1, 0, 1, image.getHeight());

      pixmap.drawPixel(rect.x - 1, rect.y - 1, image.getPixel(0, 0));
      pixmap.drawPixel(rect.x + image.getWidth(), rect.y - 1, image.getPixel(image.getWidth() - 1, 0));
      pixmap.drawPixel(rect.x - 1, rect.y + image.getHeight(), image.getPixel(0, image.getHeight() - 1));
      pixmap.drawPixel(rect.x + image.getWidth(), rect.y + image.getHeight(), image.getPixel(image.getWidth() - 1, image.getHeight() - 1));

      //pixmap.drawPixmap(image, rect.x - 1, rect.y, rect.x, rect.y + rect.height, 0, 0, 1, image.getHeight(), null);
      //pixmap.drawPixmap(image, rect.x + rect.width, rect.y, rect.x + rect.width + 1, rect.y + rect.height, image.getWidth() - 1, 0);

      //pixmap.drawPixmap(image, rect.x - 1, rect.y - 1, rect.x, rect.y, 0, 0);
      //pixmap.drawPixmap(image, rect.x + rect.width, rect.y - 1, rect.x + rect.width + 1, rect.y, image.getWidth() - 1, 0);

      //pixmap.drawPixmap(image, rect.x - 1, rect.y + rect.height, rect.x, rect.y + rect.height + 1, 0, image.getHeight() - 1);
      //pixmap.drawPixmap(image, rect.x + rect.width, rect.y + rect.height, rect.x + rect.width + 1, rect.y + rect.height + 1  image.getWidth() - 1, image.getHeight() - 1);
    }

    return true;
  }

  private Node insert(Node node, PackRectangle rect) {
    if (node.name == null && node.leftChild != null && node.rightChild != null) {
      Node newNode = insert(node.leftChild, rect);
      if (newNode == null) newNode = insert(node.rightChild, rect);

      return newNode;
    } else {
      if (node.name != null) return null;

      if (node.rect.width == rect.width && node.rect.height == rect.height) return node;

      if (node.rect.width < rect.width || node.rect.height < rect.height) return null;

      node.leftChild = new Node();
      node.rightChild = new Node();

      int deltaWidth = node.rect.width - rect.width;
      int deltaHeight = node.rect.height - rect.height;

      if (deltaWidth > deltaHeight) {
        node.leftChild.rect.x = node.rect.x;
        node.leftChild.rect.y = node.rect.y;
        node.leftChild.rect.width = rect.width;
        node.leftChild.rect.height = node.rect.height;

        node.rightChild.rect.x = node.rect.x + rect.width;
        node.rightChild.rect.y = node.rect.y;
        node.rightChild.rect.width = node.rect.width - rect.width;
        node.rightChild.rect.height = node.rect.height;
      } else {
        node.leftChild.rect.x = node.rect.x;
        node.leftChild.rect.y = node.rect.y;
        node.leftChild.rect.width = node.rect.width;
        node.leftChild.rect.height = rect.height;

        node.rightChild.rect.x = node.rect.x;
        node.rightChild.rect.y = node.rect.y + rect.height;
        node.rightChild.rect.width = node.rect.width;
        node.rightChild.rect.height = node.rect.height - rect.height;
      }

      return insert(node.leftChild, rect);
    }
  }

  public Pixmap getPixmap() {
    return pixmap;
  }

  public Map<Asset, PackRectangle> getRectangles() {
    return rectangles;
  }
}
