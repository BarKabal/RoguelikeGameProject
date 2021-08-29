package map.items;

public abstract class weapon extends item{
    protected double minDamage, maxDamage;

    public double[] minMaxDamage() {
        return new double[]{minDamage,maxDamage};
    }
}
