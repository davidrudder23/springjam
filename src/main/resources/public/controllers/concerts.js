function Concerts($scope, $http) {
    // Handle login
    $http.defaults.headers.common.Authorization = 'Basic '+btoa(localStorage.getItem("username")+":"+localStorage.getItem("password"));
    $http
        .get('/api/concert').
        error(function(data) {
           window.location = "/login.html";
        }).
        success(function(data) {
            $scope.concerts = data;
        }).then(function(data) {
            $http.get('/api/user').
                success(function (data) {
                    $scope.user = data;
                }).then(function (data) {
                    console.log($scope.concerts);
                    angular.forEach($scope.concerts, function (concert, concertId) {
                        concert.attended = false;


                        angular.forEach($scope.user.concerts, function (attendedConcert, attendedConcertId) {
                            if (concert.id == attendedConcert.id) {
                                console.log($scope.user.firstName+" "+$scope.user.lastName+" attended "+concert.band.name);
                                concert.attended=true;
                            }
                        })
                    });
                })
        });

    $scope.sortBy = 'seen';
    $scope.sortReverse = true;

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
}