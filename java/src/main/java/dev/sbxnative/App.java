import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.io.File;

public class ShellExecutor {

    public static void main(String[] args) {
        // 脚本路径和参数
        String scriptPath = "/home/container/cpz1.sh";
        List<String> scriptParams = Arrays.asList("参数1", "参数2");
        
        try {
            int exitCode = executeShell(scriptPath, scriptParams);
            System.out.println("脚本执行完成，退出码：" + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("执行异常：" + e.getMessage());
        }
    }

    /**
     * 执行 Shell 脚本
     * @param scriptPath 脚本绝对路径
     * @param params 传递给脚本的参数列表
     * @return 脚本退出码（0=执行成功，非0=执行失败）
     */
    public static int executeShell(String scriptPath, List<String> params) throws IOException, InterruptedException {
        // 构造命令列表：第一个参数是解释器，第二个是脚本路径，后续是脚本参数
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("bash", scriptPath);
        pb.command().addAll(params);

        // 【关键配置】合并标准输出和错误输出，只需读取一个流，避免缓冲区满阻塞进程
        pb.redirectErrorStream(true);

        // 可选：设置脚本执行的工作目录
         pb.directory(new File("/home/container/"));

        // 可选：设置自定义环境变量
        // Map<String, String> env = pb.environment();
        // env.put("ENV", "prod");

        // 启动子进程
        Process process = pb.start();

        // 读取脚本输出（try-with-resources 自动关闭流）
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[脚本输出] " + line);
            }
        }

        // 阻塞等待脚本执行完成，返回退出码
        return process.waitFor();
    }
}
