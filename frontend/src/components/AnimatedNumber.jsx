import { useEffect, useState, useRef } from "react";

function AnimatedNumber({
    value = 0,
    duration = 1200,
    decimals = 0,
    suffix = "",
    fromZero = false,
}) {

    const [display, setDisplay] = useState(0);
    const prevValueRef = useRef(0);

    useEffect(() => {

        const targetValue = Number(value) || 0;
        const start = fromZero ? 0 : prevValueRef.current;
        const safeDuration = Math.max(Number(duration) || 0, 1);
        const startTime = performance.now();
        let frameId;

        function animate(currentTime) {

            const progress = Math.min(
                (currentTime - startTime) / safeDuration,
                1
            );

            const eased = 1 - Math.pow(1 - progress, 3);
            const currentDisplay = start + (targetValue - start) * eased;

            prevValueRef.current = currentDisplay;
            setDisplay(currentDisplay);

            if (progress < 1) {

                frameId = requestAnimationFrame(animate);

            } else {

                prevValueRef.current = targetValue;
                setDisplay(targetValue);

            }

        }

        frameId = requestAnimationFrame(animate);

        return () => {
            if (frameId) {
                cancelAnimationFrame(frameId);
            }
        };

    }, [value, duration, fromZero]);

    return (
        <>
            {Number(display).toFixed(decimals)}
            {suffix}
        </>
    );

}

export default AnimatedNumber;