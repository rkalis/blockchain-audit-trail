(function(angular){

  var scripts = document.getElementsByTagName("script"),
    currentScriptPath = scripts[scripts.length-1].src;

  angular.module('ion-radial-progress', [])
    .directive('ionRadialProgress', ['$timeout', function($timeout) {
      return {
        restrict: 'E',
        transclue: true,
        replace: true,
        scope: {
          timer: '='
        },
        controller: function ($scope) {
          $scope.seconds = $scope.timer;

          $scope.gt50 = function () {
            return $scope.seconds > ($scope.timer/2);
          };

          function countdown() {
            function tick() {
              $scope.seconds--;

              // Calculate the amount of circle to fill in
              $scope.deg = 360*($scope.seconds/$scope.timer);

              // Are we done?
              if( $scope.seconds > 0 ) {
                setTimeout(tick, 1000);
              } else {
                //done
              }

              // Make sure we don't call $apply() at the wrong time
              $timeout(function() {
                $scope.$apply();
              });
            }
            tick();
          }

          // start the countdown
          countdown();
        },
        templateUrl: currentScriptPath.substring(0, currentScriptPath.lastIndexOf('/') + 1) + '../templates/ion-radial-progress.html'
      };
    }]);

})(window.angular);
