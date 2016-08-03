
module.exports = function(app) {

  app.animation('.roloAnimation', [ function() {

    return {

      /*******************************
      * animations and click events
      ********************************/

        enter: function() {


          let boxes  = Array.from(document.querySelectorAll('.box'));

          let time  = 15;
          let total = boxes.length;
          let step  = 1 / total;

          // optional: delay [can also reverse by setting '-step' or '-1']
          // let delay = step * time;
          let delay = 1 / total * time;

          let w1 = 600;
          let w2 = 100;
          let y1 = w1 / 2 - w2 / 2;
          let y2 = w1 - w2 + 100;
          let z1 = -200;
          let z2 = z1 / 2;

          let values = [
            { y: y1, z: 0  },
            { y: y2, z: z2 },
            { y: y1, z: z1 },
            { y: 0,  z: z2 },
            { y: y1, z: 0  },
          ];

          TweenLite.defaultEase = Linear.easeNone;

          TweenLite.set("#rolodex", {
            perspective: 200,
            rotationY: 20,
            transformStyle: "preserve-3d"
          });

          TweenLite.set(boxes, { y: y1, z: 0 });

          let bezier = { values: values, type: "soft" };

          let timeline = boxes.map(bezierTween)
            .reduce(buildTimeline, new TimelineMax());

          let pauseTween = TweenLite.to(timeline, 1, { timeScale: 0 }).reverse();

          toggle.addEventListener("click", function() {
            controlTween.reversed(!controlTween.reversed());
          });
          pause.addEventListener("click", function() {
            controlTween.pause();
          });

          function bezierTween(box) {
            return TweenMax.to(box, time, { bezier: bezier, repeat: 3 });
          }

          function buildTimeline(tl, tween, i) {
            return tl.add(tween, i * delay);
          }

          let controlTween = new TimelineMax({repeat:-1})
          controlTween.add(timeline.tweenFromTo(15, 30));

          controlTween.eventCallback("onUpdate", adjustUI)
          progressSlider.addEventListener("input", update);


          document.getElementById("resume").onclick = function() {
            controlTween.resume();
          }

          boxes.forEach(handleClick);

          function handleClick(element, i) {
            let button = document.querySelector("#" + element.dataset.button);
            button.addEventListener("click", function() {
              console.log("hey clickkie");
              tweenTo(i * step);
            });
          }

          function tweenTo(progress) {
            controlTween.pause();
            TweenLite.to(controlTween, 0.3, {progress:progress})
          }

          function update(){
              controlTween.progress(progressSlider.value).pause();
          }

          function adjustUI() {
            progressSlider.value = controlTween.progress();
          }

          $(window).scroll(function(e){
            let scrollTop = $(window).scrollTop();
            let docHeight = $(document).height();
            let winHeight = $(window).height();
            let scrollPercent = (scrollTop) / (docHeight - winHeight);
            let scrollPercentRounded = Math.round(scrollPercent*100)/100;

            timeline.progress( scrollPercent ).pause();
            progressSlider.value = scrollPercent;
          });


        }



    }

  }])
}
