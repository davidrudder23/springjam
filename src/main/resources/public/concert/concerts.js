function Concerts($scope, $http, $rootScope) {

    $rootScope.$watch('selectedBand', function() {
        if (!angular.isDefined($rootScope.selectedBand)) {
            return;
        }
        $http
            .get('/api/band/'+$rootScope.selectedBand.id)
            .success(function(data) {
                $scope.band=data;
            });

        $http.defaults.headers.common.Authorization = 'Basic '+btoa(localStorage.getItem("email")+":"+localStorage.getItem("password"));

        $scope.sortBy = 'date';
        $scope.sortReverse = false;

        $http.get('/api/band/'+$rootScope.selectedBand.id+'/yearsplayed')
            .success(function(data){
                $scope.yearsPlayed = data;
                $scope.setYear(data[data.length-1]);
            });

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
                $http.get('/api/performance/concert/'+concertId)
                    .success(function(data){
                        $scope.concert.performances = data;
                        console.log($scope.concert.performances);
                        angular.forEach($scope.concert.performances, function (performance, performanceId) {
                           $http.get('/api/song/'+performance.song)
                               .success(function(data) {
                                  performance.song = data;
                               });
                        });
                    });
            });
    }

    $scope.setYear = function(year) {
        $scope.currentYear = year;
        $http
            .get('/api/band/'+$rootScope.selectedBand.id+'/concerts/'+year).
            error(function(data) {
                window.location = "/auth/login.html";
            }).
            success(function(data) {
                $scope.concerts = data;
                $http.get('/api/concert/'+$rootScope.selectedBand.id+'/seen').
                    success(function (data) {
                        $scope.seenConcerts= data;
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
    }
}