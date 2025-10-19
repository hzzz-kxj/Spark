package SoftwareDesignPatterns.Work2;

public class PartFactory {
    public static Part getPart(String partType) {

        if(partType == null){
            return null;
        }
        if (partType.equals("circular") || partType.equals("圆形")) {
            return new circularPart();
        } else if (partType.equals("square") || partType.equals("正方形")) {
            return new squarePart();
        } else if (partType.equals("triangle") || partType.equals("三角形")) {
            return new trianglePart();
        }
        return null;
    }
}
