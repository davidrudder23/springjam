function Concerts($scope, $http, $rootScope) {

    $rootScope.$watch('selectedBand', function() {
    $http.defaults.headers.common.Authorization = 'Basic '+btoa(localStorage.getItem("email")+":"+localStorage.getItem("password"));
    $http
        .get('/api/band/'+$rootScope.selectedBand.id+'/concerts').
        error(function(data) {
           window.location = "/login.html";
        }).
        success(function(data) {
            $scope.concerts = data;
        }).then(function(data) {
            $http.get('/api/concert/'+$rootScope.selectedBand.id+'/seen').
                success(function (data) {
                    $scope.seenConcerts= data;
                    console.log($scope.concerts);
                    angular.forEach($scope.concerts, function (concert, concertId) {
                        concert.attended = false;

                        angular.forEach($scope.seenConcerts, function (attendedConcert, attendedConcertId) {
                            if (concert.id == attendedConcert.id) {
                                concert.attended=true;
                            }
                        })
                    });
                })
        });

    $scope.sortBy = 'seen';
    $scope.sortReverse = true;
    });


    $scope.toggleAttended = function(attendedConcertId) {
        angular.forEach($scope.concerts, function (concert, concertId) {
            console.log(concert.id+" attended="+concert.attended);
            if (concert.id == attendedConcertId) {
                console.log("toggling "+attendedConcertId);
                $http.get("/api/concert/attended/"+concert.id+"/"+(concert.attended?"true":"false")).
                   success(function(data) {
                        console.log(data);
                    });
            }
        });
    }

    $scope.showConcert = function(concertId) {
        $http
            .get("/api/concert/"+concertId)
            .success(function(data){
               $scope.concert = data;
            }).then(function() {
                angular.forEach($scope.concert.performances, function (performance, performanceId) {
                    $http.get("/api/song/"+performance.song)
                        .success(function(data){
                            performance.song=data;
                        });
                });
            });
    }
}