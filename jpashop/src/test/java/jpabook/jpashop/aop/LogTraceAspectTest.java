package jpabook.jpashop.aop;

import jpabook.jpashop.aop.Trace.LogTrace;
import jpabook.jpashop.aop.Trace.TraceStatus;
import jpabook.jpashop.service.MemberService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class LogTraceAspectTest {

    @Mock
    private LogTrace logTrace;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private TraceStatus traceStatus;

    @Autowired
    private LogTraceAspect logTraceAspect;

    @Autowired
    MemberService memberService;
    @BeforeEach
    void setUp() {
        logTraceAspect = new LogTraceAspect(logTrace);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("test.method()");
    }

    @Test
    @DisplayName("예외가 발생하지 않으면 LogTrace의 begin, end 메서드가 호출되어야 한다.")
    void executeSuccess() throws Throwable {
        // given
        String expectedResult = "result";
        when(logTrace.begin("test.method()")).thenReturn(traceStatus);
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // when
        Object result = logTraceAspect.execute(joinPoint);

        // then
        assertEquals(expectedResult, result);
        verify(logTrace).begin("test.method()");
        verify(logTrace).end(traceStatus);
        verify(logTrace, never()).exception(any(), any());
    }

    @Test
    @DisplayName("예외가 발생하면 LogTrace의 begin, exception 메서드가 호출되어야 한다.")
    void executeException() throws Throwable {
        // given
        Exception expectedException = new RuntimeException("테스트 예외");
        when(logTrace.begin("test.method()")).thenReturn(traceStatus);
        when(joinPoint.proceed()).thenThrow(expectedException);

        // when & then
        Exception thrownException = assertThrows(RuntimeException.class, () -> {
            logTraceAspect.execute(joinPoint);
        });

        assertEquals(expectedException, thrownException);
        verify(logTrace).begin("test.method()");
        verify(logTrace, never()).end(any());
        verify(logTrace).exception(eq(traceStatus), eq(expectedException));
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void interfaceProxy() {
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(memberService)).isFalse();
        assertThat(AopUtils.isCglibProxy(memberService)).isTrue();
    }
}