package cryptoDESX;

public class PBox {
    // tak samo jak w przypadku sboxow, zgodnie z zaleceniami dane z dokumentacji NIST
    // https://csrc.nist.gov/files/pubs/fips/46/final/docs/nbs.fips.46.pdf

    public static final int[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25,
    };

}

