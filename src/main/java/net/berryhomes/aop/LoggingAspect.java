package net.berryhomes.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(net.berryhomes.aop.Loggable) || @within(net.berryhomes.aop.Loggable)")
    public Object logEverything(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Безопасное логирование аргументов без вызова тяжелых toString() у файлов
        String safeArgs = "[]";
        if (args != null) {
            safeArgs = Stream.of(args)
                    .map(arg -> {
                        if (arg == null) return "null";
                        if (arg instanceof MultipartFile file) {
                            return "MultipartFile[name=" + file.getName() + ", size=" + file.getSize() + "]";
                        }
                        if (arg instanceof Collection<?> col && !col.isEmpty() && col.iterator().next() instanceof MultipartFile) {
                            return "List<MultipartFile>[size=" + col.size() + "]";
                        }
                        if (arg instanceof Errors) {
                            return "BindingResult";
                        }
                        return arg.toString();
                    })
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        log.info(">>> Entering [{}]. Arguments: {}", methodName, safeArgs);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("!!! Exception in [{}]: {}", methodName, e.getMessage());
            throw e;
        }

        log.info("<<< Exiting [{}]. Result: {}", methodName, result);
        return result;
    }
}