import { useEffect, useState } from "react";

function AnimatedNumber({
    value = 0,
    duration = 1200,
    decimals = 0,
    suffix = "",
    fromZero = false,
}) {

    const [display, setDisplay] = useState(0);

    useEffect(() => {

        const start = fromZero ? 0 : display;

        const startTime = performance.now();

        function animate(currentTime) {

            const progress = Math.min(
                (currentTime - startTime) / duration,
                1
            );

            const eased = 1 - Math.pow(1 - progress, 3);

            setDisplay(start + (value - start) * eased);

            if (progress < 1) {

                requestAnimationFrame(animate);

            } else {

                setDisplay(value);

            }

        }

        requestAnimationFrame(animate);

    }, [value]);

    return (
        <>
            {Number(display).toFixed(decimals)}
            {suffix}
        </>
    );

}

export default AnimatedNumber;