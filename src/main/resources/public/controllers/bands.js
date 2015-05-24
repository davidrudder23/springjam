function Bands($scope, $http) {
    $http.defaults.headers.common.Authorization = 'Basic '+btoa(localStorage.getItem("email")+":"+localStorage.getItem("password"));
    $http.get('/api/band').
        error(function(data){
            window.location = "/login.html";
        }).
        success(function(data) {
            $scope.bands = data;
        });

}