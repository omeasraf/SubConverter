import com.amiasraf.Converter;
import org.junit.Test;


public class testSubtitle {

    @Test
    public void srtToAss() {
        Converter converter = new Converter();
        var content = converter.srtToAss(getABSPath("subtitles/subtitles.srt"));
        System.out.println(content);
    }

    @Test
    public void assToSRT() {
        Converter converter = new Converter();
        var content = converter.assToSRT(getABSPath("subtitles/subtitles.ass"));
        System.out.println(content);
    }


    public String getABSPath(String path){
        ClassLoader loader = getClass().getClassLoader();
        return loader.getResource(path).getPath();
    }
}
