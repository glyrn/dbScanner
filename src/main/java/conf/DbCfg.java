package conf;

import lombok.Data;

import java.util.Map;

@Data
public class DbCfg {
    private String dbUrl;
    private String usr;
    private String pwd;
}
