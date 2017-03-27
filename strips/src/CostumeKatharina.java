import processing.core.PApplet;

public class CostumeKatharina extends Costume {



    public CostumeKatharina(PApplet base, int x, int y, String ipAddress) {
        super(base, x, y, ipAddress);
        this.ledsCount = 174;
        this.outputColors = new int[ledsCount];
    }

    @Override
    public void setSegmentColor(int segment, int color) {

        int from = 0, to = 0;

        switch (segment) {
            case 0: from = 32; to = 56;break;
            case 1: from = 96; to = 120; break;
            case 2: from = 134; to = 145; break;
            case 3: from = 160; to = 166; break;
            case 4: from = 70; to = 77; break;
            case 5: from = 6; to = 12; break;
            case 6: from = 14; to = 31; break;
            case 7: from = 78; to = 95; break;
            case 8: from = 157; to = 159; break;
            case 9: from = 167; to = 169; break;
            case 10: from = 153; to = 156; break;
            case 11: from = 170; to = 173; break;
            case 12: from = 146; to = 152; break;
            case 13: from = 128; to = 133; break;
            case 14: from = 64; to = 68; break;
            case 15: from = 0; to = 4; break;
            case 16: from = 58; to = 63; break;
            case 17: from = 122; to = 127; break;
        }
        for (int i = from; i <= to; i++) {
            outputColors[i] = color;
        }

    }
}
