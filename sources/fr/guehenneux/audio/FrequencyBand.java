

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class FrequencyBand implements Comparable<FrequencyBand> {

    private float f;

    private float f1;

    private float f2;

    private boolean band;

    /**
     * @param f1
     * @param f2
     */
    public FrequencyBand(float f1, float f2) {

        this.f1 = f1;
        this.f2 = f2;

        band = true;

    }

    /**
     * @param f
     */
    public FrequencyBand(float f) {

        this.f = f;

        band = false;

    }

    public boolean equals(Object object) {

        boolean equals;

        if (object == null) {

            equals = false;

        } else if (object == this) {

            equals = true;

        } else if (object instanceof FrequencyBand) {

            FrequencyBand that = (FrequencyBand) object;

            if (this.band) {

                if (that.band) {

                    equals = this.f1 == that.f1 && this.f2 == that.f2;

                } else {

                    equals = this.f1 <= that.f && that.f < this.f2;
                }

            } else {

                if (that.band) {

                    equals = that.f1 <= this.f && this.f < that.f2;

                } else {

                    equals = this.f == that.f;

                }

            }

        } else {

            equals = false;

        }

        return equals;

    }

    /**
     * @return f1
     */
    public float getF1() {
        return f1;
    }

    /**
     * @param f1 f1 à définir
     */
    public void setF1(float f1) {
        this.f1 = f1;
    }

    /**
     * @return f2
     */
    public float getF2() {
        return f2;
    }

    /**
     * @param f2 f2 à définir
     */
    public void setF2(float f2) {
        this.f2 = f2;
    }

    public int compareTo(FrequencyBand that) {

        int comparaison;

        if (this.band) {

            if (that.band) {

                if (this.f1 < that.f1) {
                    comparaison = -1;
                } else if (this.f1 > that.f1) {
                    comparaison = 1;
                } else {
                    comparaison = 0;
                }

            } else {

                if (that.f < this.f1) {
                    comparaison = 1;
                } else if (that.f >= this.f2) {
                    comparaison = -1;
                } else {
                    comparaison = 0;
                }

            }

        } else {

            if (that.band) {

                if (this.f < that.f1) {
                    comparaison = -1;
                } else if (this.f >= that.f2) {
                    comparaison = 1;
                } else {
                    comparaison = 0;
                }

            } else {

                if (this.f < that.f) {
                    comparaison = -1;
                } else if (this.f > that.f) {
                    comparaison = 1;
                } else {
                    comparaison = 0;
                }

            }

        }

        return comparaison;

    }

}
